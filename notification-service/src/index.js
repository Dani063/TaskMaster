const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const port = process.env.PORT || 3000;

// Middleware para analizar el cuerpo de las solicitudes
app.use(bodyParser.json());

// Importar y usar el router de notificaciones
const notificationsRouter = require('./routes/notification');
app.use('/api/notifications', notificationsRouter);

// Iniciar el servidor
app.listen(port, () => {
    console.log(`Notification service running on port ${port}`);
});
