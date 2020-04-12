(ns forebb
  (:require [clojure.java.io :as io]
            [clojure.set]
            [clojure.string :as str]))

(declare -main problems)
(load-file "problems.clj")

(defn get-problem [id]
  (some (fn [{_id :_id :as p}]
          (when (= _id id)
            p))
        problems))

(defn get-next-problem-id [id]
  (let [prob-ids (map :_id problems)
        sc       (into (sorted-set) prob-ids)]
    (first (subseq sc > id))))

(defn restricted-fns [ans res-fns]
  (let [ans-sym (into #{} (filter symbol? (flatten (read-string ans))))
        res-sym (into #{} (map symbol res-fns))]
    (clojure.set/intersection ans-sym res-sym)))

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
  (let [prob (get-problem n)
        id   (:_id prob)]
    (println (str "\n#" id ": " (:title prob)))
    (println (str "\n" (:description prob) "\n"))
    (run! println (:tests prob))
    (when-let [restricted (:restricted prob)]
      (println (colorize (str "\n" "Special Restrictions:" restricted) :red)))
    (println "")
    (spit (str "answers/" n) (read-line))))

(defn reject []
  (println (colorize "\nSorry, try again..." :red))
  (Thread/sleep 1500)
  (-main))

(defn reject-fns [coll]
  (println (colorize "\nSorry, try again..." :red))
  (println (colorize (str "You are using restricted functions: " coll) :red))
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
  (let [name       (.getName answer)
        n          (Integer/parseInt name)
        ans        (slurp answer)
        problem    (get-problem n)
        tests      (:tests problem)
        replaced   (mapv #(str/replace % "__" ans) tests)
        restricted (:restricted problem)
        bad-fns    (restricted-fns (first replaced) restricted)]
    (cond
      (= "" ans)    false
      (seq bad-fns) false
      :else         (every? true? (map safe-eval replaced)))))

(defn submit [ans n]
  (let [problem    (get-problem n)
        tests      (:tests problem)
        replaced   (map #(str/replace % "__" ans) tests)
        restricted (:restricted problem)
        bad-fns    (restricted-fns (first replaced) restricted)]
    (cond
      (= ans "")    (reject)
      (seq bad-fns) (reject-fns bad-fns)
      :else         (check (map safe-eval replaced)))))

(defn -main []
  (let [ans      (most-recent-answer)
        correct? (test-ans ans)
        ans-n    (Integer/parseInt (.getName ans))
        n        (if correct? (get-next-problem-id ans-n) ans-n)]
    (prompt n)
    (submit (slurp (str "answers/" n)) n)))

(-main)
