(ns forebb_test
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]
            [clojure.test :as t :refer [deftest is]]))

(declare problems)
(load-file "problems.clj")

(def answer-files
  (->> (io/file "answers")
       (.listFiles)
       (filter #(re-matches #".*\d+" (.getPath %)))))

(deftest forebb-test
  (doseq [^java.io.File answer-file answer-files]
    (let [name (.getName answer-file)
          n (Integer/parseInt name)
          ans (slurp answer-file)
          problem (problems (dec n))
          tests (:tests problem)
          replaced (mapv #(str/replace % "__" ans) tests)]
      (doseq [test replaced]
        (is (eval (read-string test)))))))

(let [report (t/run-tests)]
  (System/exit (+ (:fail report)
                  (:error report))))
