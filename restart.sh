docker pull freefly19/track-debts
docker pull freefly19/track-debts-frontend

docker rm -f track-debts-frontend track-debts track-debts-pg track-debts-spammer
docker network remove track-debts-net

docker network create track-debts-net
docker run -d -v /root/data:/var/lib/postgresql/data -p 5434:5432 -e POSTGRES_DB=track-debts --network track-debts-net --name track-debts-pg postgres
docker run -d --network track-debts-net -e DB_HOST=track-debts-pg -e DB_PORT=5432  --name track-debts freefly19/track-debts
docker run -d --network track-debts-net -p 80:80 --name track-debts-frontend freefly19/track-debts-frontend
docker run -d --network track-debts-net -e DB_HOST=track-debts-pg -e DB_PORT=5432 --name track-debts-spammer kolegran/td-spammer:0.3
