#!/bin/bash

# Setup script for the Raspberry Pi 3+ acting as its own tiny network with
# a Kedei LCD screen.

if [ "$EUID" != "0" ]; then
	exec sudo $0
fi

# Make sure we're using the entire root fs
raspi-config --expand-rootfs

# If the screen hasn't been enabled yet, do so now.
if ! grep -q ili9486 /sys/class/graphics/fb*/name; then
	echo "The LCD screen has not been configured yet.  Doing so will install a bunch"
	echo "of drivers and will require a reboot.  When it does reboot, log back in"
	echo "after a few minutes and re-run this script to proceed with the rest of the"
	echo "configuration."
	echo ""
	echo "Ctrl-C to cancel, or <Enter> to proceed."
	read FOO
	rm -rf LCD_driver.zip LCD_driver
	wget http://kedei.net/IMG/spi_128M/LCD_driver.zip
	unzip LCD_driver.zip
	cd LCD_driver/ && bash ./LCD35_fbcp
fi

# Install needed packages to make the system run.
apt install -y hostapd dnsmasq debconf-utils

# Configure the screen fonts so it's easily readable.
cat - > /etc/default/console-setup <<CONSOLE
# Created by setup.sh

ACTIVE_CONSOLES="/dev/tty[1-6]"

CHARMAP="UTF-8"

CODESET="guess"
FONTFACE="TerminusBold"
FONTSIZE="12x24"

VIDEOMODE=
CONSOLE
dpkg-reconfigure -f noninteractive console-setup

# Setup DHCP
cat - > /etc/dnsmasq.d/wlan0.conf <<DNSMASQ_WLAN0
dhcp-range=192.168.250.100,192.168.250.200,12h
DNSMASQ_WLAN0
cat - > /etc/dnsmasq.d/eth0.conf <<DNSMASQ_ETH0
dhcp-range=192.168.249.100,192.168.249.200,12h
DNSMASQ_ETH0
systemctl restart dnsmasq

# Setup wifi
raspi-config nonint do_wifi_country US
cat - > /etc/network/interfaces.d/wlan0.conf <<WLAN0
allow-hotplug wlan0
iface wlan0 inet static
	address 192.168.250.1
	netmask 255.255.255.0
	up sysctl -w net.ipv4.ip_forward=1
WLAN0
ifdown wlan0 2>/dev/null
ifup wlan0 2>/dev/null
cat - > /etc/hostapd/hostapd.conf <<HOSTAPD
interface=wlan0
driver=nl80211
ssid=RiverBots
country_code=US
hw_mode=g
channel=6
macaddr_acl=0
auth_algs=1
ignore_broadcast_ssid=0
wpa=2
wpa_passphrase=the ROBOT uprising
wpa_key_mgmt=WPA-PSK
wpa_pairwise=CCMP
wpa_group_rekey=86400
ieee80211n=1
wme_enabled=1
HOSTAPD
egrep -v '^DAEMON_CONF' /etc/default/hostapd > /etc/default/hostapd.tmp
echo "DAEMON_CONF=/etc/hostapd/hostapd.conf" >> /etc/default/hostapd.tmp
mv -f /etc/default/hostapd.tmp /etc/default/hostapd
systemctl unmask hostapd
systemctl restart hostapd

# Setup ethernet
cat - > /etc/network/interfaces.d/eth0.conf <<ETH0
auto eth0
iface eth0 inet static
	address 192.168.249.1
	netmask 255.255.255.0
	up sysctl -w net.ipv4.ip_forward=1
ETH0

# Write out the status daemon that updates the screen.
cat - > /usr/local/bin/status <<STATUS
#!/bin/bash

while true; do
	clear
	echo "SSID: RiverBots"
	echo "Pass: the ROBOT uprising"
	echo "Known IPs:"
	arp -an | awk '!/incomplete/ { print \$NF ": " \$2 }' | sort
	sleep 5
done
STATUS
chmod 755 /usr/local/bin/status
mkdir -p /etc/systemd/system/getty\@tty1.service.d
cat - > /etc/systemd/system/getty\@tty1.service.d/override.conf <<TTY
[Service]
ExecStart=
ExecStart=-/usr/local/bin/status
StandardInput=tty
StandardOutput=tty
TTY

echo "The system has been configured.  After reboot,"
echo "DO NOT PLUG THE ETHERNET INTO AN ACTIVE NETWORK!!!"
echo "REALLY!!!"
echo "MAKE SURE YOU'RE NOT PLUGGED INTO A NORMAL NETWORK!!!"
echo ""
echo "Press <Enter> to proceed with reboot."
read FOOBAR

reboot
