# IMPLEMENTACIÓN DEL PATRON SAGA 

Contexto :
Se desea realizar la matricula de un estudiante en un curso,a continuacion se describen los pasos a seguir para llevar a cabo este proceso utilizando el patrón Saga para manejar las transacciones distribuidas y asegurar la consistencia de los datos.
- 1.- EL estudiante solicita la matricula en un curso. (Enrollment)
- 2.- El sistema verifica si el estudiante realizo el pago de la matricula. (Payment)
- 3.- Si el pago es exitoso, se procede a registrar al estudiante en el curso. ( Enrollment)
- 4.- Si el pago no se realizo correctamente, se cancela la solicitud de matricula. ( Enrollment)

<img src="./saga_kafka.png" alt="Diagrama del Patrón Saga" width="600"/>