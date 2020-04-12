# 4bb

Run 4clojure problems with babashka right from your shell. Even do it over ssh!

## Usage

1. Install [babashka](https://github.com/borkdude/babashka/) (v0.0.75 or later required)
2. Clone this repo and type `bb -f 4bb.clj` while in the project
   directory. Running with `rlwrap` is recommended for enabling history with up
   and down arrows.

Your answers and progress are stored in the `answers` directory.

## Test

To run tests:

```
$ bb tests.clj
```

## TODO

- [x] Reject answers containing "restricted" functions
