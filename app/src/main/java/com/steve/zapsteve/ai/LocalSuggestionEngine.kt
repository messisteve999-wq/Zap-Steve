package com.steve.zapsteve.ai

/**
 * Small rule-based starter engine: works offline, requires no API key and no model download.
 * It is intentionally simple; it is not a full language model.
 */
object LocalSuggestionEngine {
    fun generate(message: String, sender: String = "ami"): List<Pair<String, String>> {
        val m = message.trim()
        val lower = m.lowercase()
        if (m.isBlank()) return listOf(
            "Relance" to "Tu veux me raconter ce qui se passe ? 🙂",
            "Amical" to "Je t'écoute, vas-y 😊",
            "Question" to "Et toi, comment tu vois les choses ?"
        )

        if (containsAny(lower, listOf("désolé", "desole", "pardon", "excuse-moi", "excuse moi"))) {
            return listOf(
                "Bienveillant" to "Merci de me l'avoir dit. On peut en parler tranquillement 🙂",
                "Apaisant" to "T'inquiète, l'important c'est qu'on puisse se comprendre.",
                "Sincère" to "J'apprécie que tu sois venu(e) m'en parler.",
                "Question" to "Qu'est-ce qui s'est passé de ton côté ?",
                "Naturel" to "Pas de souci, parlons-en calmement."
            )
        }
        if (containsAny(lower, listOf("triste", "ça va pas", "ca va pas", "fatigué", "fatigue", "problème", "probleme", "pleure"))) {
            return listOf(
                "Empathique" to "Oh mince… je suis là si tu as envie d'en parler ❤️",
                "Attentionné" to "Tu veux me raconter ce qui te pèse ? Je t'écoute.",
                "Amical" to "Je t'envoie un peu de courage. Tu n'es pas obligé(e) de tout gérer seul(e).",
                "Doux" to "On peut prendre le temps, pas besoin de tout expliquer d'un coup.",
                "Question" to "Qu'est-ce qui pourrait te faire du bien en ce moment ?"
            )
        }
        if (containsAny(lower, listOf("salut", "bonjour", "coucou", "hello", "bonsoir", "hey"))) {
            return listOf(
                "Drôle" to "Heeey 😄 J'allais justement recruter quelqu'un pour rendre ma journée moins ordinaire. Ça va ?",
                "Amical" to "Coucou 😊 Comment se passe ta journée ?",
                "Flirt léger" to "Hey toi 😉 Alors, quelle bonne nouvelle tu m'apportes ?",
                "Curieux" to "Salut ! Qu'est-ce qui t'a fait sourire aujourd'hui ?",
                "Relance" to "Raconte-moi un petit truc sympa de ta journée 😄"
            )
        }
        if (containsAny(lower, listOf("tu fais quoi", "que fais-tu", "quoi de neuf", "ça dit quoi", "ca dit quoi"))) {
            return listOf(
                "Drôle" to "J'essaie d'avoir l'air occupé(e), mais ton message a interrompu ma carrière 😂 Et toi ?",
                "Amical" to "Je me pose un peu. Et toi, tu es sur quoi en ce moment ?",
                "Flirt léger" to "Je cherchais une conversation sympa… le timing est plutôt bon 😉",
                "Curieux" to "Rien d'extraordinaire pour l'instant. Quel est ton programme ?",
                "Sujet" to "Si tu pouvais être n'importe où maintenant, tu choisirais quel endroit ?"
            )
        }
        if (containsAny(lower, listOf("merci", "thanks"))) {
            return listOf(
                "Amical" to "Avec plaisir 😊",
                "Drôle" to "Je prends les remerciements… les compliments aussi, si jamais 😂",
                "Chaleureux" to "Ça me fait plaisir, vraiment !",
                "Relance" to "Et sinon, comment se passe le reste de ta journée ?",
                "Naturel" to "Pas de souci, compte sur moi 🙂"
            )
        }

        val short = m.take(110)
        return listOf(
            "Naturel" to "Ah oui ? Raconte-moi un peu plus 😊",
            "Drôle" to "Attends, il me faut la version complète avec les détails croustillants 😂",
            "Amical" to "Je vois ce que tu veux dire. Et toi, tu l'as vécu comment ?",
            "Flirt léger" to "J'aime bien discuter avec toi, tu sais 😉",
            "Question" to "Et qu'est-ce qui s'est passé ensuite ?"
        ).map { it.first to it.second }
    }

    private fun containsAny(text: String, needles: List<String>) = needles.any { text.contains(it) }
}
