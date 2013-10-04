***************************************
*    C O O L   C A T   P A C M A N    *
***************************************

By Daniel Ingmer Jallov, daja@itu.dk

Package: daja.coolcat.EvolvedPacman
Dependencies: PhenoBean, Search

INSTRUCTIONS:
Import package to src folder and use new EvolvedPacman() in exec.runGame.
If this for some reason should give a NoSuchFileException, copy the bestGene.txt
file to a data folder and use new EvolvedPacman("<data_folder>/bestGene.txt").

DESCRIPTION:
Finite state machine that uses a limited breadth first search to determine which path to
choose. The search runs for a set depth and assigns negative values to paths with 
non-edible ghosts and positive values to paths with pills. The path with the 
highest score is chosen.