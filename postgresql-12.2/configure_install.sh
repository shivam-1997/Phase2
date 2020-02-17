#! /bin/sh

DIR=~

if [ -d "$FILE" ]; then
    echo "$DIR exists."
else 
	mkdir $DIR
    echo "$DIR created."
fi

export POSTGRES_INSTALLDIR="$DIR/postgres" 

# export POSTGRES_SRCDIR="."
# cd ${POSTGRES_SRCDIR}

./configure --prefix=${POSTGRES_INSTALLDIR} --enable-debug
export enable_debug=yes


sudo apt-get install libreadline6-dev zlib1g-dev bison flex

make | tee gmake.out
make install | tee gmake_install.out


echo "*****************************************************"

export LD_LIBRARY_PATH=${POSTGRES_INSTALLDIR}/lib:${LD_LIBRARY_PATH}
export PATH=${POSTGRES_INSTALLDIR}/bin:${PATH}
export PGDATA=${POSTGRES_INSTALLDIR}/data


# creating new POSTGRESQL database cluster
initdb -D ${PGDATA}

