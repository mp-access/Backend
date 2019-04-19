if [ "$1" = "docker" ]; then
    echo "Removing data from docker volume"
    docker rm $(docker ps -a -q) -f
    docker volume prune
elif [ "$1" = "standalone" ]; then
    echo "Removing data from standalone"
    rm -rf "$2/standalone/data"
    rm -rf "$2/standalone/tmp"
fi
