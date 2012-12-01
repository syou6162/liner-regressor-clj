(defproject liner-regressor-clj "0.0.1"
  :description "Liner regression written in clojure"
  :url "https://github.com/syou6162/liner-regressor-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [info.yasuhisay/clj-utils "0.1.1"]
                 [org.clojure/tools.cli "0.2.1"]
                 [org.clojure/algo.generic "0.1.0"]
                 [info.yasuhisay/clj-utils "0.1.1"]]
  :jvm-opts ["-Xmx8G" "-server" "-Dfile.encoding=UTF-8"]
  :main liner-regressor-clj.core)
