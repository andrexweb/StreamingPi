StreamingPi
============

Ejecucion sin parametros (ver StramingPiServer):

```Shell
pi@raspberrypi ~ $ java -cp ./StreamingPi-1.0-SNAPSHOT.jar 
```

Se debe ejecutar estos parametros, luego de haber ejecutado el StramingPiServer en el raspberry pi.

Se pone la ip y puerto donde se esta ejecutando el StreamingPiServer (si es el puerto por defecto es el 1024):
```
CONNECT ip puerto
```
Se configura el puerto por donde se va a transmitir el streaming;
```
SETPORT 4445
```
Se ejecuta uno de los siguientes comandos:

Para Video:
```
RUNPI raspivid -n -t 0 -w 640 -h 480 -fps 10 -o -
```
o para modo foto:
```
RUNPI raspistill -n -t 0 -tl 150 -th 0:0:0 -w 640 -h 480 -q 5 -o -
```

Para detener:
```
STOP
```

Para salir, presione Ctrl + C


______________________________
Raspberry Pi is a trademark of the Raspberry Pi Foundation, http://www.raspberrypi.org

