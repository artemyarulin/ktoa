(defproject ktoa "0.0.1-SNAPSHOT"
  :description "Set of useful helpers and wrappers around React Native for ClojureScript development"
  :url "https://github.com/artemyarulin/ktoa"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [react-native-externs "0.0.1-SNAPSHOT"]
  :plugins [[lein-cljsbuild "1.1.0"]]
  :source-paths ["src"]
  :cljsbuild {:builds {:repl {:source-paths ["src"]}}})
