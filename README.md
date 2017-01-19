# Subspace
An Android app for auto downloading of subtitles for movies and series.

## App Goals
1. Monitor a folder (or multiple) on the file system where content is downloaded to.
2. If it finds a new file or folder, it should automatically download an english subtitle for it.
3. It should only try that if the file is complete - aka, not partially downloaded.
4. It should not attempt to download more than once a day if it hasn’t found anything and not more than 3 times.
5. It should still have a manual option to download a subtitle for a chosen file.

## Tech considerations
The idea is for the app to use the SubDB API - http://thesubdb.com/api/ at least initially. But it should be coded with an interface to allow API changes.
