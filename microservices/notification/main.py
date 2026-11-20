import os
from contextlib import asynccontextmanager

import py_eureka_client.eureka_client as eureka_client
import uvicorn
from fastapi import FastAPI

# Le port vient de la variable d'environnement PORT (8084 par defaut)
# => on peut lancer plusieurs instances sur des ports differents
PORT = int(os.getenv("PORT", "8084"))
HOST = os.getenv("HOST", "localhost")
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Au demarrage : enregistrement dans Eureka
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="NOTIFICATION",
        instance_id=f"{HOST}:notification:{PORT}",
        instance_host=HOST,
        instance_ip="127.0.0.1",
        instance_port=PORT,
        status_page_url=f"http://{HOST}:{PORT}/api/notifications/hello",
        health_check_url=f"http://{HOST}:{PORT}/api/notifications/hello",
    )
    print(f"Notification enregistre dans Eureka (port {PORT})")
    yield
    # A l'arret : on arrete le client Eureka (desinscription)
    await eureka_client.stop_async()
    print("Notification desinscrit de Eureka")


app = FastAPI(title="Notification", lifespan=lifespan)


@app.get("/api/notifications/hello")
def hello():
    return f"Hello from NOTIFICATION (port {PORT})"


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=PORT)
