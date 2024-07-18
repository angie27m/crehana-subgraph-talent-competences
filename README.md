# Crehana subgraph Talent Competences 

Este proyecto contiene los graphs usados para el modulo de Evaluaciones de competencias de evaluadores. Aquí se pueden listar las evaluaciones activas de una empresa y de un colaborador, el listado de las personas que se deben evaluar y la lógica de guardado de la evaluación de una persona. Está construído con el framework Springboot con la dependencia **Spring for GraphQL**.

## Construído con 🛠️

* [Springboot](https://start.spring.io/) - El framework web usado
* [Maven](https://maven.apache.org/) - Manejador de dependencias
* [Java 17]- Version de Java

## Prerequisitos

 Clonar el repositorio git clone https://gitlab.com/crehana-team/talent/crehana-subgraph-talent-competences.git o se clona dependiendo de la configuración que desee el desarrollador por https o por ssh, para que funcione el proyecto también se debe clonar el proyecto https://gitlab.com/crehana-team/talent/acsendo-api-core.git que se encuentra en el siguiente repositorio: https://gitlab.com/crehana-team/talent/acsendo-api-core. Este proyecto Acsendo-api-core contiene todas las entidades de base de datos, repositorios, daos y demás lógica de persistencia y consulta a base de datos, por lo cual este repositorio se comparte con el proyecto de Feedback.


## Despliegue 📦

Para desplegar el proyecto en entorno local, el desarrollador debe tener instalado un ID de desarrollo o descargar **Spring Tools 4 for Eclipse** en la página https://spring.io/tools (se descarga según el sistema operativo usado). Una vez descargado se abre el ejecutable, se importa el proyecto: Para esto se hace clic en File/Open projects from file systems, en la ventana que aparece se debe seleccionar la ruta donde se clonó el repositorio y se hace clic en finalizar. 

Ya con el proyecto agregado,  se da clic derecho en el proyecto/Run As/ Spring Boot App.

## Autores ✒️

* **Erika Parra** - *Desarrollador* - [erika.parra@crehana.com]
* **Angie Manrique** - *Desarrollador* - [angie.manrique@crehana.com]





