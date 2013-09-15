package com.componentix.nlp.stemmer.uk

def stemmer = new Stemmer()

int total = 0
int matches = 0
new File("uk_stems.txt").eachLine {
    if (!it) return;

    def (word, expected) = it.split(",")
    def stemmed = stemmer.stem(word)
    def isMatching = (stemmed == expected)
    println "${isMatching ? '✓' : '✗'} $word -> $stemmed \t$expected"

    if (isMatching) matches++
    total++
}

println "Total words: $total"
println "Total matches: $matches"
def accuracy = (float)matches / total * 100
println "Accuracy: $accuracy%"

