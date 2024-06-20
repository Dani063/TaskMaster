const express = require('express');
const router = express.Router();
const nodemailer = require('nodemailer');

// Configuración del transporte de correo (ejemplo con Gmail)
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'taskmasternotificationservice@gmail.com',
    pass: 'plrn huwm tynt pudx'
  }
});

// Endpoint para enviar una notificación
router.post('/send', async (req, res) => {
  const { to, subject, text } = req.body;

  const mailOptions = {
    from: 'taskmasternotificationservice@gmail.com',
    to,
    subject,
    text
  };

  try {
    await transporter.sendMail(mailOptions);
    res.status(200).send('Notification sent successfully');
  } catch (error) {
    console.error('Error sending notification:', error);
    res.status(500).send('Error sending notification');
  }
});

module.exports = router;
