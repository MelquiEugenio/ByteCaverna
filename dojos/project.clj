(defproject dojos "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                     :plugins [[lein-midje "3.1.3"]]}})
