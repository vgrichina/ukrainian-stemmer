import groovy.transform.Field

@Field def word_ends = """
  а ам ами ах та
  в вав вавсь вався вала валась валася вали вались валися вало валось валося вати ватись ватися всь вся
  е еві ем ею
  є ємо ємось ємося ється єте єтесь єтеся єш єшся єю
  и ив ий ила или ило илося им ими имо имось имося ите итесь итеся ити ить иться их иш ишся
  й ймо ймось ймося йсь йся йте йтесь йтеся
  і ів ій ім імо ість істю іть
  ї
  ла лась лася ло лось лося ли лись лися
  о ові овував овувала овувати ого ої ок ом ому осте ості очка очкам очками очках очки очків очкові очком очку очок ою
  ти тись тися
  у ував увала увати
  ь
  ці
  ю юст юсь юся ють ються
  я ям ями ях
""".trim().split(/\s+/)

// WAT ?
// к ка кам ками ках ки кою ку
// ні ню ня ням нями нях
@Field def wends = word_ends.sort {-it.length()}

// endings in unchangable words

@Field def stable_endings = """
  ер
  ск
""".trim().split(/\s+/)

@Field def skip_ends = stable_endings.sort {-it.length()}

// endings are changing
@Field def change_endings = [
  "аче" : "ак",
  "іче" : "ік",
  "йовував" : "йов", "йовувала" : "йов", "йовувати" : "йов",
  "ьовував" : "ьов", "ьовувала" : "ьов", "ьовувати" : "ьов",
  "цьовував" : "ц", "цьовувала" : "ц", "цьовувати" : "ц",
  "ядер" : "ядр"
]

// words to skip
@Field def stable_exclusions = """
  баядер беатріче
  віче
  наче неначе
  одначе
  паче
""".trim().split(/\s+/)

// words to replace
@Field def exclusions = [
  "відер" : "відр",
  "був" : "бува"
]


def stem(word) {
    // normalize word
    word = replaceStressedVowels(word);

    // don't change short words
    if (word.length() <= 2 ) return word;

    // check for unchanged exclusions
    if (stable_exclusions.contains(word)) {
        return word;
    }

    // check for replace exclusions
    if (exclusions[word]) {
        return exclusions[word];
    }

    // changing endings
    // TODO order endings by abc DESC
    for (eow in change_endings.keySet().sort { change_endings[it] }) {
        if (word.endsWith(eow)) {
            return word.substring(0, word.length() - eow.length()) + change_endings[eow]
        }
    }

    // match for stable endings
    for (eow in skip_ends) {
        if (word.endsWith(eow)) {
            return word
        }
    }

    // try simple trim
    for (eow in wends) {
        if (word.endsWith(eow)) {
            def trimmed = word.substring(0, word.length() - eow.length())
            if (trimmed.length() > 2) {
                return trimmed
            }
        }
    }

    return word
}



/*
 * Replace Ukrainian stressed vowels to unstressed ones
 */
def replaceStressedVowels(word) {
    def nagolos = [
        "а́" : "а",
        "е́" : "е",
        "є́" : "є",
        "и́" : "и",
        "і́" : "і",
        "ї́" : "ї",
        "о́" : "о",
        "у́" : "у",
        "ю́" : "ю",
        "я́" : "я"
    ]

    return word.toLowerCase().collect { nagolos[it] ?: it }.join("")
}


int total = 0
int matches = 0
new File("uk_stems.txt").eachLine {
    if (!it) return;

    def (word, expected) = it.split(",")
    def stemmed = stem(word)
    def isMatching = (stemmed == expected)
    println "${isMatching ? '✓' : '✗'} $word -> $stemmed \t$expected"

    if (isMatching) matches++
    total++
}

println "Total words: $total"
println "Total matches: $matches"
def accuracy = (float)matches / total * 100
println "Accuracy: $accuracy%"

