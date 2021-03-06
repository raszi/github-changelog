(def +version+ "0.1.0-SNAPSHOT")

(set-env!
 :source-paths #{"src/"}
 :dependencies
 '[[org.clojure/clojure "1.8.0"]
   [clj-jgit "0.8.9"]
   [clj-http "3.0.0"]
   [cheshire "5.6.1"]
   [throttler "1.0.0"]
   [org.slf4j/slf4j-nop "1.7.21"]
   [grimradical/clj-semver "0.3.0" :exclusions [org.clojure/clojure]]
   [org.clojure/tools.cli "0.3.5"]
   ; testing
   [org.clojure/test.check "0.9.0" :scope "test"]
   [clj-http-fake "1.0.2" :scope "test"]
   [adzerk/boot-test "1.1.1" :scope "test"]
   [tolitius/boot-check "0.1.1" :scope "test"]
   [boot/core "2.5.5" :scope "provided"]
   [adzerk/bootlaces "0.1.13" :scope "test"]])

(require
 '[adzerk.boot-test :as boot-test]
 '[tolitius.boot-check :as check]
 '[adzerk.bootlaces :refer :all])

(bootlaces! +version+)

(task-options!
 pom {:project 'github-changelog
      :version +version+
      :description "GitHub changelog"
      :url "https://github.com/raszi/github-changelog"
      :license {"MIT" "http://choosealicense.com/licenses/mit/"}}
 jar {:file "github-changelog.jar"
      :main 'github-changelog.cli}
 aot {:namespace #{'github-changelog.cli}})

(deftask testing-helper
  "Sets up the environment for testing"
  []
  (merge-env! :source-paths #{"test"})
  identity)

(deftask check-sources
  "Checks source code for possible improvements/simplifications"
  []
  (comp
   (testing-helper)
   (check/with-bikeshed)
   (check/with-eastwood)
   (check/with-yagni)
   (check/with-kibit)))

(deftask dev []
  "Sets up a development environment"
  (comp (testing-helper)
        (repl)))

(ns-unmap 'boot.user 'test)

(deftask test
  "Runs the test suite"
  []
  (comp
   (testing-helper)
   (boot-test/test)))

(deftask auto-test
  "Tests everything whenever source changes"
  []
  (comp
   (testing-helper)
   (watch)
   (boot-test/test)))

(deftask uberjar
  []
  (comp
   (uber)
   (aot)
   (pom)
   (jar)
   (sift)
   (target)))
