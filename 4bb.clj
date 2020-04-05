(ns forebb
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]))

(declare -main problems)
(load-file "problems.clj")

(defn most-recent-answer []
  (->> (io/file "answers")
       (.listFiles)
       (sort-by #(.lastModified %))
       last))

(def ansi-styles
  {:red   "[31m"
   :green "[32m"
   :reset "[0m"})

(defn ansi [style]
  (str \u001b (style ansi-styles)))

(defn colorize
  [text color]
  (str (ansi color) text (ansi :reset)))

(defn prompt [n]
  (let [prob (nth problems (dec n))
        id   (:_id prob)]
    (println (str "\n#" id ": " (:title prob)))
    (println (str "\n" (:description prob) "\n"))
    (run! println (:tests prob))
    (println "")
    (spit (str "answers/" n) (read-line))))

(defn reject []
  (println (colorize "\nSorry, try again..." :red))
  (Thread/sleep 1500)
  (-main))

(defn next-prob! []
  (println (colorize "\nNICE! Here's the next one:" :green))
  (println (char 7))
  (Thread/sleep 1500)
  (-main))

(defn check [results]
  (if (every? true? results)
    (next-prob!)
    (reject)))

(defn safe-eval [ans]
  (try (eval (read-string ans))
       (catch Exception _
         false)))

(defn test-ans [answer]
  (let [name     (.getName answer)
        n        (Integer/parseInt name)
        ans      (slurp answer)
        problem (some (fn [{id :_id :as p}]
                        (when (= id n)
                          p))
                      problems)
        tests    (:tests problem)
        replaced (mapv #(str/replace % "__" ans) tests)]
    (if (= "" ans) false
        (every? true? (map safe-eval replaced)))))

(defn submit [ans n]
  (let [tests (:tests (problems (dec n)))
        replaced (map #(str/replace % "__" ans) tests)]
    (if (= ans "") (reject)
        (check (map safe-eval replaced)))))

(defn -main []
  (let [ans (most-recent-answer)
        correct? (test-ans ans)
        ans-n (Integer/parseInt (.getName ans))
        n (if correct? (inc ans-n) ans-n)]
    (prompt n)
    (submit (slurp (str "answers/" n)) n)))

(-main)
