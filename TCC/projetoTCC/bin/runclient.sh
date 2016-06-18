
if [ $# -gt 1 ]; then
sh smartrun.sh client.ClientTest $1 $2 $3 $4 $5 $6 $7 $8
else
sh smartrun.sh client.ClientConsole $1 $2 $3 $4 $5 $6 $7 $8
fi


