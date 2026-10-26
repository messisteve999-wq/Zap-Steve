# Plan de tests — Zap Steve 0.1.0

## Test 1 : lancement
- Installer `app-debug.apk`.
- Ouvrir Zap Steve.
- Vérifier que l'écran d'accueil apparaît.

## Test 2 : autorisation de superposition
- Appuyer sur Autoriser la fenêtre flottante.
- Accorder l'autorisation Android.
- Revenir et afficher la fenêtre.
- Vérifier qu'elle reste au-dessus d'une autre application.
- Déplacer la fenêtre en faisant glisser son en-tête.

## Test 3 : moteur local
- Appuyer sur Exemple.
- Vérifier que cinq suggestions apparaissent.
- Copier une suggestion.
- Coller dans une application de notes pour vérifier le presse-papiers.

## Test 4 : notifications
- Activer l'accès aux notifications.
- Faire apparaître une notification WhatsApp ou Messenger contenant un texte.
- Vérifier si la fenêtre se met à jour.
- Répéter avec l'application SMS du téléphone.

## Test 5 : limites et stabilité
- Tester avec les notifications masquées et visibles.
- Fermer puis rouvrir Zap Steve.
- Vérifier le comportement après redémarrage du téléphone.
- Vérifier que rien n'est envoyé automatiquement.
- Tester l'usage mémoire et la fluidité.

## Résultat à noter
Pour chaque test : Réussi / Échoué, application testée, texte visible dans la notification, message d'erreur éventuel.
