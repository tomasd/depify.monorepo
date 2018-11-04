# depify.monorepo
Utilies for converting lein monorepo into deps.edn monorepo


# Project description

Project consists of following utilities:

* `depify.projects`
Recursively visit all subprojects within current directory and 
generates `deps.edn` as a result of converting `project.clj` 

* `depify.generate-overrides`
Generate `:override-deps` section of `deps.edn` where all subproject 
references are overriden with `:local/root`.

* `depify.cljsbuild`
Generates figwheel build files for all `project.clj` containing
`:cljsbuild` configurations.

# Usage

Create an alias in your `~/.clojure/deps.edn` map:

```clojure
:depify.monorepo {:extra-deps             {depify.monorepo     {:git/url "https://github.com/tomasd/depify.monorepo"
                                                                :sha     "e42f4f42b279b33096ed3660df34a963b226db9b"}}}
```

Then, invoke *depify.monorepo* alias in root folder of your monorepo project with one of the utilities.

Examples:

Generate deps.edn for all monorepo subprojects:
```bash
clojure -A:depify.monorepo -m depify.projects
```

Genereate overrides for local development:
```bash
clojure -A:depify.monorepo -m depify.generate-overrides
```

Generate figwheel clojurescript builds:
```bash
clojure -A:depify.monorepo -m depify.cljsbuild
```