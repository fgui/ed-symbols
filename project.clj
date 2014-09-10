(defproject ed-symbols "0.1.0-SNAPSHOT"
  :description "a simple memory game"
  :license {:name "Eclipse"
           :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [
            [lein-cljsbuild "1.0.3"]
            [lein-ancient "0.5.4"]
            ]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2322"]
                 [reagent "0.4.2"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [compojure "1.1.9"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 ]
  :main ed-symbols.server/run
  :source-paths ["src/clj"]
  :cljsbuild {
              :builds
              {
               :dev {:source-paths ["src/cljs"]
                     :compiler
                     {:output-to
                      "resources/public/cljs/symbols.js"
                      ;;:source-map "resources/public/cljs/symbols-dev.js.map"
                      :optimizations :whitespace
                      :preamble ["reagent/react.js"]
                      :pretty-print true}}
               :prod {:source-paths ["src"]
                      :compiler
                      {:output-to
                       "resources/public/cljs/symbols.js"
                       :optimizations :advanced
                       :preamble ["reagent/react.min.js"]
                       :pretty-print false}}} }
  )
