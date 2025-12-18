1. Para poder executalo programa, recoméndase:
- Executar mínimo o cliente desde terminal.

- Ter instalado jdk-21

- Nas termináis que se vaian empregar, declarar:
	$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
	$env:Path="$env:JAVA_HOME\bin;" + $env:Path
  para declarar jdk-21 como jAVA_HOME temporal nesa terminal.

- Buildear desde o root do proxecto:
	./gradlew clean build

- No caso de que se execute o servidor desde terminal:
	./gradlew :server:bootRun

- Executar cliente:
	./gradlew :client:run


2. Para poder entrar á aplicación, o correo vinculado co Spotify necesita ser engadido
   ao User Manegement do Dashboard da API vinculada con ShareCloud (contactar cos alumnos para engadilo).
- Se non, os correos e contrasinais empregados por nós son: 

	    CORREO				CONTRASINAL
	lupe@gmail.com 				11111
	joaquinquin.abia@gmail.com		11111
