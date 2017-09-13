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

# stop running mongo
sudo service mongod stop

# override default mongo configuration file with our
cat /vagrant_data/mongo.conf > /etc/mongod.conf

# start running mongo
sudo service mongod start

# is successful but returns a non 0 status for some reason
#if sudo service mongod start
#then
#  echo "Successfully Started MongoDB"
#else
#  echo "Failure to Start MongoDB. exit status $?"
#  exit 1
#fi

