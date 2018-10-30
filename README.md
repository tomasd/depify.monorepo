# depify.monorepo
Converts lein monorepo into deps.edn monorepo


# Project description

`depify.monorepo` reads your monorepo project structure and generates `deps.edn` with all 
dependencies found in `project.clj` files. Monorepo submodules are referenced using `:local/root`.
These files are suitable for [Clojure CLI tools](https://clojure.org/guides/deps_and_cli]).

If you are looking for single `project.clj` conversion, [depify](https://github.com/hagmonk/depify) 
is better suited for this purpose. depify.monorepo is built on top of depify with 
tools needed for monorepos.

`deps.edn` and the Clojure CLI tools have a narrower focus than Leiningen or
Boot, but *depify.monorepo* will do its best to produce a useful `deps.edn` replacement.
This includes adding extra aliases to provide "missing" functionality. One such
example is the addition of the `:test` and `:runner` aliases borrowed from Sean
Corfield's [[https://github.com/seancorfield/dot-clojure/blob/master/deps.edn#L9-L19][dot-clojure]] repo. Other aliases may be added in the future - PRs are
always welcome!

# Usage

Create an alias in your `~/.clojure/deps.edn` map:

```clojure
:depify.monorepo  {:extra-deps             {org.clojure/clojure {:mvn/version "1.9.0"}
                                            depify.monorepo     {:git/url "https://github.com/tomasd/depify-monorepo"
                                                                :sha     "320594a57330f33c6d164c0f0472deade7df8689"}}
                   :main-opts               ["-m" "depify.monorepo"]}
```

Then, invoke *depify.monorepo* in root folder of your monorepo project. `depify.monorepo` will do it's 
best to find all the `project.clj` files:

```bash
clj -A:depify.monorepo
```

*depify.monorepo* will read any pre-existing `deps.edn` file in your subproject folder and use
that as an initial starting point. The result of merging ~project.clj~ into
deps.edn will be written back to corresponding `deps.edn`.
