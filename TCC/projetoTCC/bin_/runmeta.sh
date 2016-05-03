
case "$2" in
	"0" )
	var=2 
	;;
	"1" )
	var=2
	;;
	"5" )
	var=4
	;;
	* )
	echo "runmeta <id> <raid>"
esac

sh smartrun.sh server.meta.ServerConsole $1 $2 $var $3 $4 $5 $6 $7 $8
