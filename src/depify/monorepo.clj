(ns depify.monorepo
  (:require
    depify.project
    [clojure.java.io :as io]
    [clojure.pprint :as pprint])
  (:import (java.nio.file Paths)))

(defn relative-path [base file]
  (-> (Paths/get (-> (io/file base)
                     (.toURI)))
      (.relativize (Paths/get (-> (io/file file)
                                  (.toURI))))
      (.toFile)))

(defn in-checkouts-directory? [file]
  (= "checkouts" (some-> file
                         (.getParentFile)
                         (.getParentFile)
                         (.getName))))

(defn read-project [project-clj-file]
  (let [deps-edn-file (io/file (.getParentFile project-clj-file)
                               "deps.edn")
        deps-edn      (depify.project/get-deps-edn deps-edn-file)
        project-clj   (depify.project/get-project-clj project-clj-file)]
    (-> (depify.project/read-prj deps-edn project-clj)
        (assoc ::project-clj project-clj-file)
        (assoc ::project-path (.getParent project-clj-file))
        (assoc ::deps-edn deps-edn-file))))

(defn update-internal-dependencies [deps-edn local-path internal-dependencies]
  (update
    deps-edn
    :deps (fn [deps]
            (->> deps
                 (map (fn [[dep m]]
                        (if-some [internal-dep (get internal-dependencies dep)]
                          [dep (-> m (assoc :local/root (.getPath (relative-path local-path internal-dep))))]
                          [dep m])))
                 (into {})))))

(defn seq-project-clj [directory]
  (->> (file-seq (io/file directory))
       (filter #(= (.getName %) "project.clj"))
       (remove in-checkouts-directory?)))

(defn process [directory]
  (let [projects              (->> (seq-project-clj directory)
                                   (map read-project))
        internal-dependencies (->> projects
                                   (map (juxt :name ::project-path))
                                   (into {}))]
    (->> projects
         (map (fn [project]
                [(::deps-edn project) (-> (depify.project/process project)
                                          (update-internal-dependencies (::deps-edn project) internal-dependencies))])))))

(defn -main [& args]
  (doseq [[target deps-edn] (process (io/file "."))]
    (with-open [writer (io/writer target)]
      (println "Writing " (.getPath target))
      (pprint/pprint deps-edn writer))))