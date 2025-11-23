# KoogMultiagentProject

Ce projet utilise [Gradle](https://gradle.org/).

Mises en place spécifiques Koog + Ollama (par rapport à la documentation Koog Getting Started):

Ce qui ne correspondait pas à la doc initialement et a été corrigé:
- Aucune dépendance Koog n'était déclarée dans le module d'application → ajout de `ai.koog:koog-agents` dans `app/build.gradle.kts`.
- La classe principale pointait vers `fr.nicolaslinard.koog.kmp.app.AppKt` mais `AppKt.kt` était vide → ajout d'un `main` minimal.
- Le README ne mentionnait pas l'installation/lancement d'Ollama ni le modèle à tirer → instructions ajoutées ci‑dessous.

Pré-requis (Ollama):
1) Installer Ollama: https://ollama.com/
2) Lancer le service Ollama (il écoute par défaut sur http://localhost:11434)
3) Tirer un modèle compatible (exemples):
   - `ollama pull llama3.2`
   - ou `ollama pull qwen2.5`

Construire et exécuter l'application:
- macOS/Linux: `./gradlew run`
- Windows: `gradlew.bat run`

Commandes utiles Gradle:
- `./gradlew run` pour builder et exécuter
- `./gradlew build` pour builder
- `./gradlew check` pour lancer les vérifications/tests
- `./gradlew clean` pour nettoyer

Notes:
- Les dépôts Maven (Maven Central) sont déjà configurés dans `settings.gradle.kts`.
- La JVM ciblée est 21 (via plugin de convention dans `buildSrc`).
- Le code d'exemple actuel vérifie simplement la disponibilité d'Ollama et affiche un message. Vous pouvez ensuite intégrer les appels Koog suivant la documentation: https://docs.koog.ai/getting-started/#ollama_1