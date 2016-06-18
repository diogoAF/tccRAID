if [ $# -eq 0 ]; then
echo "latencytest.sh [w|r] [size]" 
else
sh runclient.sh 7001 $1 $2 1 1000
fi

