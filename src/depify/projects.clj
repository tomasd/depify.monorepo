(ns depify.projects
  (:require
    [leiningen.core.project :as project]
    [clojure.java.io :as jio]
    [clojure.java.io :as io]
    [clojure.pprint :as pprint]
    [depify.lein :as lein]))

(defn process-monorepo [base-path]
  (doseq [lein-project (lein/seq-project-clj base-path)]
    (let [target (jio/file (.getParentFile lein-project) "deps.edn")]
      (with-open [writer (io/writer target)]
        (println "Writing " (.getPath target))
        (pprint/pprint (lein/lein-project->deps-edn (project/read-raw (.getAbsolutePath lein-project))) writer)))))

(defn -main [& args]
  (process-monorepo (io/file ".")))