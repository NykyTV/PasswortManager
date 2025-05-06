# Passwort Manager

## JDK-22 is required

```
- grafische Oberfläche
- Verschlüsselung
- Passwort generator
- Suchfunktion
- (optional mit Server)
```
Used Icon:
`https://www.flaticon.com/de/kostenloses-icon/passwortmanager_5206964`

---

## Install PasswortManager Server (Debian 11)

```bash
wget -O install.sh https://raw.githubusercontent.com/NykyTV/PasswortManager/refs/heads/develop/server-install/install.sh`
sh install.sh
```

## Ports
Forward following ports:
```text
- 8080/tcp (phpMyAdmin)
- 3306/tcp (MySQL)
```

<details>
<summary>Manual Installation</summary>


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
  mysql:
    image: mysql:latest
    container_name: mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: deinPasswort
      MYSQL_DATABASE: passwortmanager
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"

  phpmyadmin:
    image: phpmyadmin/phpmyadmin
    container_name: phpmyadmin
    restart: unless-stopped
    ports:
      - "8080:80"
    environment:
      PMA_HOST: mysql
      PMA_PORT: 3306
      PMA_USER: root
      PMA_PASSWORD: deinPasswort

volumes:
  mysql_data:
```
</details>

**phpMyAdmin is optional!**

used in this project:
[JDBC Driver/Mysql Connector](https://dev.mysql.com/downloads/connector/j/)

## Start Docker container
```bash
docker compose up -d
```

## Settings in PasswordManager
Beispiel:

```text
Datenbank-URL: jdbc:mysql://192.168.2.22:3306/passwortmanager
Benutzername: root
Passwort: deinPasswort
```
