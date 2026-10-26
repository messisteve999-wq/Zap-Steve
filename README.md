# Zap Steve — Android prototype 0.1.0

Prototype Android natif Kotlin, thème sombre/vert, suggestions hors ligne basées sur des règles, fenêtre flottante et lecture de notifications autorisées.

## Ce qui fonctionne dans cette version

- Écran d'accueil avec accès aux réglages Android.
- Fenêtre flottante déplaçable au-dessus des applications après autorisation.
- Champ de test pour saisir un message.
- Cinq suggestions de réponse avec catégories et bouton **Copier**.
- Moteur local simple : aucun compte, aucune clé API et aucun accès Internet nécessaire.
- Service d'écoute des notifications pour certaines applications ciblées.
- Aucun envoi automatique de message.

## Limites connues du prototype

- Le moteur local est un ensemble de règles et de modèles de phrases, pas un grand modèle d'IA. Il ne comprend pas réellement toute la conversation.
- La détection dépend du contenu présent dans les notifications Android. Les messages masqués, notifications groupées, contenus médias ou restrictions constructeur peuvent empêcher la détection.
- La première version ne filtre les notifications que par identifiants de paquets courants. Certains SMS ou clients alternatifs peuvent demander un ajustement.
- Android peut empêcher le démarrage d'une fenêtre depuis une notification en arrière-plan. Si cela arrive, ouvre Zap Steve puis démarre la fenêtre manuellement.
- L'interface de la fenêtre est volontairement simple et conçue pour un premier test sur un téléphone peu puissant.

## Installer les autorisations sur Android 14

1. Installe l'APK debug.
2. Ouvre Zap Steve.
3. Appuie sur **Autoriser la fenêtre flottante** et active l'autorisation dans les réglages Android.
4. Reviens à Zap Steve et appuie sur **Autoriser l'accès aux notifications**. Active Zap Steve dans la liste Android.
5. Retourne à l'application et appuie sur **Afficher la fenêtre Zap Steve**.
6. Teste le bouton **Exemple** puis **Copier**.
7. Pour tester les notifications, demande à quelqu'un de t'envoyer un message. Selon le téléphone et les réglages, il faut que son texte soit visible dans la notification.

N'accorde l'accès aux notifications qu'à une application dont tu comprends les implications : cet accès peut exposer le contenu des notifications d'autres applications au service autorisé.

## Construire l'APK avec GitHub Actions (recommandé)

Le dépôt contient `.github/workflows/android-build.yml`. Il utilise JDK 17, Android SDK platform 35 et Gradle 8.9.

1. Crée un dépôt GitHub nommé `Zap-Steve`.
2. Téléverse le contenu de ce ZIP dans le dépôt (ou pousse le dossier depuis Termux).
3. Ouvre l'onglet **Actions**.
4. Choisis **Build Zap Steve APK**, puis **Run workflow**, ou pousse un commit sur `main`.
5. Lorsque le workflow réussit, ouvre son exécution.
6. Dans **Artifacts**, télécharge `Zap-Steve-debug-APK`.
7. Décompresse l'archive et installe `app-debug.apk` sur ton téléphone.

Le workflow produit un APK de débogage. Android peut afficher un avertissement lors de l'installation d'une application hors Play Store. N'installe que les APK provenant de ton propre dépôt.

## Envoyer le projet depuis Termux

Après avoir extrait le ZIP dans `~/Zap-Steve` et créé le dépôt vide sur GitHub :

```bash
pkg update
pkg install git unzip
cd ~/storage/downloads
unzip Zap-Steve-source.zip -d ~/Zap-Steve
cd ~/Zap-Steve

git init
git branch -M main
git add .
git commit -m "Initial Zap Steve Android prototype"
git remote add origin https://github.com/TON-COMPTE/Zap-Steve.git
git push -u origin main
```

Remplace `TON-COMPTE` par ton nom GitHub et utilise un token GitHub si Git te le demande. Ne partage jamais ton token.

## Compiler localement depuis Termux (facultatif)

La compilation Android locale sur Termux peut être difficile, surtout avec seulement 2 Go de RAM et un environnement Android SDK incomplet. GitHub Actions est donc la voie conseillée.

Si un JDK 17, Android SDK platform 35, build-tools 35.0.0 et Gradle 8.9 sont réellement disponibles :

```bash
cd ~/Zap-Steve
gradle --no-daemon assembleDebug
```

APK attendu si la compilation réussit :

`app/build/outputs/apk/debug/app-debug.apk`

Le projet utilise Android Gradle Plugin 8.7.3 et Gradle 8.9. Un Gradle 9.x n'est pas un remplacement garanti pour cette version de plugin.

## Arborescence

- `app/`: application native Kotlin.
- `ai/LocalSuggestionEngine.kt`: suggestions hors ligne.
- `overlay/FloatingWindowService.kt`: panneau flottant.
- `notifications/MessageNotificationListener.kt`: détection via notifications.
- `.github/workflows/android-build.yml`: compilation APK dans GitHub Actions.

## Coût

- Code et outils : gratuits.
- GitHub Actions : consommation soumise aux quotas et limites du compte GitHub.
- Moteur local : gratuit, sans API.
- Le projet ne nécessite pas de serveur ni de compte IA.
