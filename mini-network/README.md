# Miniature Network Using Raspberry Pi

## Introduction

To configure the RoboRIO and various other components, it's often handy
to have a captive network with a screen that displays the IP addresses
currently in use.  We've implemented such an animal and it's documented
here, along with how to configure it.

## Hardware

We use a Raspberry Pi 3+ (with WiFi and ethernet), along with a cheap
KeDei touchscreen LCD and a case to hold it all.  As is standard with
Raspberry Pi devices, we boot and run from a MicroSD card.

## Software

Setup of the Pi is basically using the
[Raspberry Pi Imager](https://www.raspberrypi.com/software/), which
will download and install the software on any Windows, Linux, or MacOS
machine.  Just download the program, run it, and put in the appropriate
MicroSD to create a bootable SD card.  We used the Lite image, but
it doesn't make a huge amount of difference.  Make sure to turn on
ssh and set up a password and whatnot before proceeding!

Username should be "pi" and password should be "the ROBOT uprising"
for this machine.

Once you have a booted machine, go ahead and plug it (temporarily)
into any ethernet network and run:

`
sudo apt install git
git clone https://github.com/riverbots42/Robot-of-2022
cd Robot-of-2022/mini-network && ./setup.sh
`

The setup program does the rest.  When it's complete (it will reboot
to configure the LCD screen the first time; you'll need to log back in),
it will prompt you to REMOVE the device from the network it's plugged into
and will act as a tiny private network when it reboots.

The display will show the SSID/Password and the list of machines it
knows about.  So if you hook up the RoboRIO to the ethernet and have any
PC connected to it on wifi, you'll see both IPs.  If the RoboRIO is
showing an IP like "eth0: 192.168.249.104", then you should be able to
visit http://192.168.249.104 from the PC, e.g.

Demo video on [Youtube](https://youtu.be/PFahzJVxMME).
