# Passwort Manager

## JDK-22 is required

```
- grafische Oberfläche
- Verschlüsselung
- Passwort generator
- Suchfunktion
```

(optional mit Server)

1. Swing UI Designer installieren
2. Git installieren
3. GitHub Account verbinden

![image](https://github.com/user-attachments/assets/c111eb5e-bc7f-4ee1-b64f-51e9bf583291)

## Install PasswortManager Server (Debian 11)

used in this project:
[JDBC Driver/Mysql Connector](https://dev.mysql.com/downloads/connector/j/)

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install curl -y
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
```

create docker-compose.yml and add this content:
```yml
version: '3.8'

services:
  mariadb:
    image: mariadb:latest
    container_name: mariadb
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: geheim123
      MYSQL_DATABASE: passwortmanager
    volumes:
      - mariadb_data:/var/lib/mysql
    networks:
      - internal

  phpmyadmin:
    image: phpmyadmin/phpmyadmin
    container_name: phpmyadmin
    restart: always
    ports:
      - 8080:80
    environment:
      PMA_HOST: mariadb
      PMA_USER: root
      PMA_PASSWORD: geheim123
    networks:
      - internal

volumes:
  mariadb_data:

networks:
  internal:
```

start docker container
```bash
docker compose up -d
```

```SQL
CREATE DATABASE passwortmanager;
USE passwortmanager;

CREATE TABLE user_dateien (
    accountName VARCHAR(255) PRIMARY KEY,
    datei LONGBLOB
);

```

OPTIONAL: phpMyAdmin
