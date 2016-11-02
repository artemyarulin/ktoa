(defproject ktoa "0.2.0-SNAPSHOT"
  :description "Set of useful helpers and wrappers around React Native for ClojureScript development"
  :url "https://github.com/artemyarulin/ktoa"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.211"]
                 [react-native-externs "0.0.2-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :source-paths ["src"]
  :cljsbuild {:builds {:repl {:source-paths ["src"]}}})
