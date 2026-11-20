const express = require('express');
const { Eureka } = require('eureka-js-client');

// Le port vient de la variable d'environnement PORT (8083 par defaut)
// => on peut lancer plusieurs instances sur des ports differents
const PORT = parseInt(process.env.PORT, 10) || 8083;
const HOST = process.env.HOST || 'localhost';
const EUREKA_HOST = process.env.EUREKA_HOST || 'localhost';
const EUREKA_PORT = parseInt(process.env.EUREKA_PORT, 10) || 8761;

const app = express();
app.use(express.json());

// Endpoint hello
app.get('/api/meetings/hello', (req, res) => {
  res.send(`Hello from MEETING (port ${PORT})`);
});

// Configuration du client Eureka
const client = new Eureka({
  instance: {
    app: 'MEETING', // nom du service dans Eureka
    instanceId: `${HOST}:meeting:${PORT}`, // unique pour chaque instance
    hostName: HOST,
    ipAddr: '127.0.0.1',
    statusPageUrl: `http://${HOST}:${PORT}/api/meetings/hello`,
    healthCheckUrl: `http://${HOST}:${PORT}/api/meetings/hello`,
    port: {
      $: PORT,
      '@enabled': true,
    },
    vipAddress: 'meeting',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: EUREKA_HOST,
    port: EUREKA_PORT,
    servicePath: '/eureka/apps/',
  },
});

app.listen(PORT, () => {
  console.log(`Meeting demarre sur le port ${PORT}`);

  // Enregistrement dans Eureka au demarrage
  client.start((error) => {
    if (error) {
      console.log('Erreur enregistrement Eureka :', error.message);
    } else {
      console.log('Meeting enregistre dans Eureka sous le nom MEETING');
    }
  });
});

// A l'arret (Ctrl+C), on se desinscrit d'Eureka
function arreter() {
  client.stop(() => {
    console.log('Meeting desinscrit de Eureka');
    process.exit(0);
  });
}
process.on('SIGINT', arreter);
process.on('SIGTERM', arreter);
