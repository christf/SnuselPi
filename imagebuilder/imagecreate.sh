#!/bin/bash

dlfile=raspbian_lite_latest
[[ -f $dlfile ]] || wget -O $dlfile https://downloads.raspberrypi.org/raspbian_lite_latest

#find baseimage in this ugly zip output
while read
do
	case $REPLY in
	*inflating*.img*) baseimage=${REPLY##*: };;
	*);;
	esac
done < <(unzip -o $dlfile)

image=${baseimage}_rebuilt-$(date -I).img
mv $baseimage $image

startsect=$(/sbin/fdisk  -lu $image|grep img2|awk '{print $2}')
lodev=$(sudo /sbin/losetup --show -f -o $((startsect*512)) $image)

# image vergrößern
dd if=/dev/zero of=$image bs=2M oflag=append conv=notrunc count=1024
lastsect=$(echo $(/sbin/fdisk  -lu $image|grep  $image|grep sectors|rev|cut -d" " -f2|rev) -1|bc -l)

# Partitionstabelle bearbeiten: 2. Partition (rootfs) löschen und an der selben Position neu anlegen. 
# Endsektor ist der letzte Sektor des Images
# http://superuser.com/questions/332252/creating-and-formating-a-partition-using-a-bash-script
# danach fs vergrößern und Dateibaum vom nfsroot reinsynchronisieren
echo -e "\nd\n2\nn\np\n2\n${startsect}\n${lastsect}\np\nw\nq" | /sbin/fdisk $image
lodev=$(sudo /sbin/losetup --show -f -o $((startsect*512)) $image)
sudo /sbin/e2fsck -fy $lodev
sudo /sbin/resize2fs $lodev

mkdir -p /tmp/ip
mkdir -p /tmp/sp
sudo /bin/mount freenas:/mnt/data/rootnfs/snuselpi /tmp/sp
sudo /bin/mount $lodev /tmp/ip

sudo rsync -aAxl --delete /tmp/sp/ /tmp/ip/|| echo some files could not be transferred.

sudo /bin/umount /tmp/sp && rm -rf /tmp/sp
sudo /bin/umount /tmp/ip && rm -rf /tmp/ip
sudo /sbin/losetup -d $lodev
cat $image |lzma -7 >$image.lzma

