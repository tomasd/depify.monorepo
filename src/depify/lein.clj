(ns depify.lein
  (:require [leiningen.core.project :as project]
            [clojure.java.io :as io]))


(defn extract-paths [project]
  (->> (concat (:source-paths project)
               (:resource-paths project))
       distinct
       vec))

(defn extract-test-paths [project]
  (->> (:test-paths project)
       distinct
       vec))

(defn remove-nil-vals [m]
  (->> m
       (remove (comp nil? val))
       (into {})))

(defn dep->symbol [{:keys [group-id artifact-id]}]
  (symbol group-id artifact-id))

(defn lein-project->deps-edn [project]
  {:paths     (extract-paths project)
   :deps      (->> (:dependencies project)
                   (map project/dependency-map)
                   (map (fn [{:keys [version exclusions classifier] :as dep}]
                          [(dep->symbol dep) (remove-nil-vals {:mvn/version version
                                                               :exclusions  (some->> exclusions
                                                                                     (map dep->symbol)
                                                                                     seq
                                                                                     vec)
                                                               :classifier  classifier})]))
                   (into {}))
   :aliases   (-> (->> (:profiles project)
                       (map (fn [[profile m]]
                              [profile {:extra-paths (extract-paths m)}]
                              ))
                       (into {}))
                  (assoc :test {:extra-paths (extract-test-paths project)}))

   :mvn/repos (some->> (:repositories project)
                       (map (fn [[repo-id repo]]
                              [repo-id (select-keys repo [:url])])))})

(defn in-checkouts-directory? [file]
  (= "checkouts" (some-> file
                         (.getParentFile)
                         (.getParentFile)
                         (.getName))))

(defn seq-project-clj [directory]
  (->> (file-seq (io/file directory))
       (filter #(= (.getName %) "project.clj"))
       (remove in-checkouts-directory?)))