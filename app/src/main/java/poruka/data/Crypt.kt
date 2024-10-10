package poruka.data

class Crypt {
    // Kotlin Alphabet and Lookup Table equivalent
    val alphabet = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "Å", "Ä", "Ö", " "  // Include space in the alphabet
    )

    // Day lookup (3-character sequences)
    val dayLookup = listOf(
        "1do", "1se", "1ch", "1pa", "1jo", "1sh", "1ie", "2do", "2se", "2ch", "2pa", "2jo", "2sh",
        "2ie", "3do", "3se", "3ch", "3pa", "3jo", "3sh", "3ie", "4do", "4se", "4ch", "4pa", "4jo", "4sh", "4ie", "5do", "5se"
    )

    // Create the lookup table
    fun createLookupTable(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        for (i in alphabet.indices) {
            val adjInd = (12 + i - 1) % alphabet.size
            val currentDate = dayLookup[adjInd]
            val currentLetter = alphabet[i]
            result[currentDate] = currentLetter
            result[currentLetter] = currentDate
        }

        return result
    }

    // Encrypt a message using the lookup table
    fun encrypt(message: String, lookupTable: Map<String, String>): String {
        var result = ""

        for (char in message) {
            val letter = char.toString()
            result += lookupTable[letter] ?: letter  // If the character is not in the lookup table, keep it unchanged
        }

        return result
    }

    // Decrypt a message using the lookup table
    fun decrypt(message: String, lookupTable: Map<String, String>): String {
        var result = ""
        var i = 0

        while (i < message.length) {
            val letter = if (i + 3 <= message.length) {
                message.substring(i, i + 3)
            } else {
                message.substring(i, message.length)  // Handle the last part of the message if it's less than 3 characters
            }

            result += lookupTable[letter] ?: letter  // If not found in lookup table, return the character as is
            i += 3
        }

        return result
    }
}

