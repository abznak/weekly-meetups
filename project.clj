(defproject weekly-meetups "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [clj-time "0.6.0"]
                 [de.ubercode.clostache/clostache "1.3.1"]
                 [ring "1.1.8"]
                 [listora/again "0.1.0"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [compojure "1.1.5"]]
  :main weekly-meetups.core
  :uberjar-name "weekly-meetups.jar"
  :min-lein-version "2.0.0"
  :warn-on-reflection true
  :ring {:handler weekly-meetups.web/app}
  :plugins [[lein-ring "0.8.5"]])
