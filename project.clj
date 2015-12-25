(defproject ktoa "0.0.1-SNAPSHOT"
  :description "Set of useful helpers and wrappers around React Native for ClojureScript development"
  :url "https://github.com/artemyarulin/ktoa"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :source-paths ["src"]
  :cljsbuild {:builds {:repl {:source-paths ["src"]}}})
