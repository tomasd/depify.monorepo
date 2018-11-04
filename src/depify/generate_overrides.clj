(ns depify.generate-overrides
  (:require
    [leiningen.core.project :as project]
    [clojure.java.io :as io]
    [clojure.pprint]
    [depify.lein :as lein]))

(defn process-override [base-path]
  {:override-deps (->> (lein/seq-project-clj base-path)
                       (map (fn [lein-project]
                              (let [project (project/read-raw (.getAbsolutePath lein-project))]
                                [(symbol (:group project) (:name project))
                                 {:local/root (.getCanonicalPath (io/file lein-project ".."))}])))
                       (into {}))})

(defn -main [& args]
  (clojure.pprint/pprint (process-override (io/file "."))))

