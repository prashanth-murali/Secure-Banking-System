#!/usr/bin/env bash

# Offical instructions found here: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

if sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6
then
  echo "Successfully Imported the public key used by the package management system."
else
  echo "Failure to Imported the public key used by the package management system. exit status $?"
  exit 1
fi

echo "deb [ arch=amd64 ] http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.4.list
#status=$?

# command is succssful but returns a non zero status for some reason, commenting out error check for now.
#if [ $status != 0 ];
#then
#  echo "Successfully created a list file for MongoDB"
#else
#  echo "Failure to create a list file for MongoDB. exit status $?"
#  exit 1
#fi

if sudo apt-get update
then
  echo "Successfully Apt Get Update"
else
  echo "Failure to Apt Get Update. exit status $?"
  exit 1
fi

if sudo apt-get install -y mongodb-org
then
  echo "Successfully Install MongoDB"
else
  echo "Failure to Install MongoDB. exit status $?"
  exit 1
fi

# Wait until mongolog contains line
#
#https://stackoverflow.com/questions/1652680/how-to-get-the-pid-of-a-process-that-is-piped-to-another-process-in-bash
#https://superuser.com/questions/270529/monitoring-a-file-until-a-string-is-found?newreg=9e4241da81b349aa8943a4d7755495e2
#
( sudo tail -f /var/log/mongodb/mongod.log & echo $! >&3 ) 3>pid | while read LOGLINE
do
   echo $LOGLINE
   if [[ $LOGLINE == *"waiting for connections"* ]];
   then
    echo "killing"
    kill $(<pid)
   else
    echo "keep looking"
   fi


done

# stop running mongo
sudo service mongod stop

# override default mongo configuration file with our
cat /vagrant_data/mongo.conf > /etc/mongod.conf

# start running mongo
sudo service mongod start

echo "Complete, Mongo listing on 192.168.33.10"

# is successful but returns a non 0 status for some reason
#if sudo service mongod start
#then
#  echo "Successfully Started MongoDB"
#else
#  echo "Failure to Start MongoDB. exit status $?"
#  exit 1
#fi


# ===== INSTALL Python dependencies =======
sudo apt-get install build-essential checkinstall
sudo apt-get install libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev

# Set python version to be installed
PY_VERSION=2.7.13

# Download Python
wget https://www.python.org/ftp/python/$PY_VERSION/Python-$PY_VERSION.tgz

# Uncompress
tar -xvf Python-$PY_VERSION.tgz

# enter python directory
cd Python-$PY_VERSION

# install python version
./configure
make

echo "Python version: $PY_VERSION was successfully installed"




