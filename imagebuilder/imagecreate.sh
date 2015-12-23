#!/bin/bash
nfslocation=freenas:/mnt/data/rootnfs/snuselpi 
nfspath=/tmp/sp
imagepath=/tmp/ip
dlurl=https://downloads.raspberrypi.org/raspbian_lite_latest


trapfunc()
{
	cleanup
	exit 1
}

cleanup()
{
	sudo /bin/umount $nfspath && rm -rf $nfspath
	sudo /bin/umount $imagepath && rm -rf $imagepath 
	sudo /sbin/losetup -d $lodev
}

trap 'trapfunc' 1 2 3 6 9 13 14 15

dlfile=raspbian_lite_latest
[[ -f $dlfile ]] || wget -O $dlfile $dlurl
#find baseimage in this ugly zip output
while read
do
	case $REPLY in
	*inflating*) baseimage=$(echo ${REPLY##*: } |tr -dc [1-9,a-z,A-Z,_,-]);;
	*);;
esac
done < <(unzip -o $dlfile)
set -ix
image="${baseimage}_rebuilt-$(date -I).img"
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
echo -e "\np\nd\n2\nn\np\n2\n${startsect}\n${lastsect}\np\nw\nq" | /sbin/fdisk $image
lodev=$(sudo /sbin/losetup --show -f -o $((startsect*512)) $image)
sudo /sbin/e2fsck -fy $lodev
sudo /sbin/resize2fs $lodev

mkdir -p $imagepath
mkdir -p $nfspath
sudo /bin/mount $nfslocation $nfspath || trapfunc
sudo /bin/mount $lodev $imagepath || trapfunc

sudo rsync -aAxl --delete $nfspath $imagepath || echo some files could not be transferred.

cleanup
lzma -7 $image

trap - 1 2 3 6 9 13 14 15
