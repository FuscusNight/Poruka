package poruka.data

class Crypt {
    // Kotlin Alphabet and Lookup Table equivalent
    val alphabet = listOf(
        "!", "?", "/", ":", ";", "'",
        ",", "$", "%", "#", "-", "A", "B",
        "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T", "U", "V", "W",
        "X", "Y", "Z", "Å", "Ä", "Ö", " ",
        "a", "b", "c", "d", "e", "f", "g", "h",
        "i", "j", "k", "l", "m", "n", "o",
        "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "å", "ä", "ö",
        "š", "č", "ć", "ž", "đ", ".", "Š",
        "Č", "Ć", "Ž", "Đ", "Š", "+", "="
    )


    // Day lookup (3-character sequences)
    val dayLookup = listOf(
        "1do", "1se", "1ch", "1pa", "1jo", "1sh", "1ie",
        "2do", "2se", "2ch", "2pa", "2jo", "2sh", "2ie",
        "3do", "3se", "3ch", "3pa", "3jo", "3sh", "3ie",
        "4do", "4se", "4ch", "4pa", "4jo", "4sh", "4ie",
        "5do", "5se", "5ch", "5pa", "5jo", "5sh", "5ie",
        "6do", "6se", "6ch", "6pa", "6jo", "6sh", "6ie",
        "7do", "7se", "7ch", "7pa", "7jo", "7sh", "7ie",
        "8do", "8se", "8ch", "8pa", "8jo", "8sh", "8ie",
        "9do", "9se", "9ch", "9pa", "9jo", "9sh", "9ie",
        "1po", "1ut", "1sr", "1ce", "1pe", "1su", "1ne",
        "2po", "2ut", "2sr", "2ce", "2pe", "2su", "2ne",
        "3po", "3ut", "3sr", "3ce", "3pe", "3su", "3ne",
    )


    // Create the lookup table
    fun createLookupTable(): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // Iterate through both 'alphabet' and 'dayLookup' lists and create two-way mappings:
        // - One that maps the 3-character code to its corresponding character.
        // - One that maps each character to its corresponding 3-character code.
        for (i in alphabet.indices) {
            val currentLetter = alphabet[i] // Get a character from the alphabet
            val currentDate = dayLookup[i]  // Get the corresponding 3-character code

            // Add both forward (code -> letter) and backward (letter -> code) mappings to the result
            // Create two-way mapping: letter to code and code to letter
            result[currentDate] = currentLetter
            result[currentLetter] = currentDate
        }

        return result
    }

    // Encrypt a message by converting each character to its corresponding 3-character code.
    // If the character is not found in the lookup table, it will remain unchanged.
    fun encrypt(message: String, lookupTable: Map<String, String>): String {
        var result = ""

        for (char in message) {
            val letter = char.toString() // Convert character to a string
            result += lookupTable[letter] ?: letter  // If the character is not in the lookup table, keep it unchanged
        }

        return result
    }

    // Decrypt a message by converting each 3-character code back to its corresponding character.
    // If the 3-character code is not found in the lookup table, it will remain unchanged.
    fun decrypt(message: String, lookupTable: Map<String, String>): String {
        var result = ""
        var i = 0

        // Iterate through the message in chunks of 3 characters at a time
        while (i < message.length) {
            val letter = if (i + 3 <= message.length) {
                message.substring(i, i + 3) // Get 3-character chunk
            } else {
                message.substring(i, message.length)  // Handle the last part of the message if it's less than 3 characters
            }

            result += lookupTable[letter] ?: letter  // If not found in lookup table, return the character as is
            i += 3
        }

        return result
    }
}

