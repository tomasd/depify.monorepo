(ns depify.cljsbuild
  (:require [leiningen.core.project :as project]
            [clojure.java.io :as io]
            [clojure.pprint]
            [clojure.java.io :as jio]
            [depify.lein :as lein]))

(defn process-build [build]
  (let [{:keys [id compiler]} build]
    [id (with-meta compiler
                   {:watch-dirs ["src"]})]))

(defn process-project [project]
  (->> (get-in project [:cljsbuild :builds])
       (map process-build)))


(defn process-cljs [base-path]
  (doseq [project-clj-path (lein/seq-project-clj base-path)]
    (let [project-clj (project/read-raw (.getCanonicalPath project-clj-path))]
      (when (get-in project-clj [:cljsbuild :builds])
        (doseq [[build-id build] (process-project project-clj)]
          (let [target-path (.getCanonicalPath (io/file project-clj-path ".." (str build-id ".cljs.edn")))]
            (println "Writing " target-path)
            (with-open [writer (jio/writer target-path)]
              (.write writer "^")
              (clojure.pprint/pprint (meta build) writer )
              (clojure.pprint/pprint build writer ))))))))

(defn -main [& args]
  (process-cljs (io/file ".")))