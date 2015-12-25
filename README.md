# ktoa 
[![Clojars Project](http://clojars.org/ktoa/latest-version.svg)](http://clojars.org/ktoa)

Set of useful helpers and wrappers around React Native for ClojureScript development. Handy in case you want to write cross platform Om-Next components

## Features

- Creates figwheel bridge file, no need to maintain any additional JS files in your repo. In your app create:
``` clojure

;; cat src/repl/repl.cljs

(ns repl.repl
  (:require [ktoa.repl :refer [start-repl]]))

(start-repl {:app-name "RootViewRN" ;; same as your RCTRootView using
             :base-url "http://localhost:3449/js"
             :root-ns  "app.core"})
             
;; cat project.clj

(defproject om-next-ios-pure "0.1.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.189"]
                 [figwheel-sidecar "0.5.0-SNAPSHOT"]
                 [com.cemerick/piggieback "0.2.1"]
                 [ktoa "0.0.1-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]
  :source-paths ["src"]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                 :init (do (use 'figwheel-sidecar.repl-api)(start-figwheel!))}
  :cljsbuild {:builds {:repl {:source-paths ["src/repl" ]
                              :compiler {:optimizations :advanced
                                         :output-to "ios/js/app.js"}}
                       :dev {:source-paths ["src/app" ]
                             :figwheel true
                             :compiler {:main app.core
                                        :output-dir "resources/public/js"}}}})

```
Then simply run `lein cljsbuild once repl && lein repl` and open your RN project. nrepl is supported as well                

- ReactNative AppRegistry magic is resolved - simply run `ktoa/register!`
- om-next requires `React` global and certain props inside it - ktoa handles it automatically
- `ktoa/om-options` returns map of required items in order to run Om-Next on RN. ktoa abstracts it for you
- `ktoa/[view text text-input]` - component helpers
- `ktoa/react-native?` - for writing cross-platform CLJS

## Example

See (om-next-cross-platform-template)[https://github.com/artemyarulin/om-next-cross-platform-template] for usage example

## Status

Early development, experimenting with right lib design. Integrating it with my app. Although I'm using Om-Next ktoa should be framework agnostick - if something doesn't work for your favorite one or you with to extend this lib - PR are very welcome!

# Credits

Big thanks to decker405 and his awesome idea how to use Figwheel with RN: https://github.com/decker405/figwheel-react-native
