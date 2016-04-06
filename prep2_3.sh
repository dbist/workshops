# bug in adding second interface, needs more work, until then, start manually

SANDBOX_FILE=~/Downloads/HDP_2.3.2_virtualbox.ova
SANDBOX_OLD_NAME="Hortonworks Sandbox with HDP 2.3.2"
SANDBOX_NEW_NAME=$1

# import appliance
vboxmanage import --options keepallmacs $SANDBOX_FILE 

# rename appliance
vboxmanage modifyvm "$SANDBOX_OLD_NAME" --name $SANDBOX_NEW_NAME

# add second NIC
vboxmanage modifyvm $SANDBOX_NEW_NAME --nic2 hostonly --hostonlyadapter1 vboxnet0

# start VM
# vboxmanage startvm $SANDBOX_NEW_NAME --type headless
