using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Win32;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            var pci = Registry.LocalMachine.OpenSubKey("SYSTEM\\CurrentControlSet\\Enum\\PCI");
            var keys = pci.GetSubKeyNames();

            foreach (var keyname in keys)
            {
                var subkey = pci.OpenSubKey(keyname);
                var devices = subkey.GetSubKeyNames();

                foreach (var devname in devices)
                {
                    var devkey = subkey.OpenSubKey(devname);
                    object val = devkey.GetValue("FriendlyName", "");
                    if (val == null || (string)val == "")
                    {
                        val = devkey.GetValue("DeviceDesc", "");
                        if (val == null) val = "";
                    }

                    Console.WriteLine((string)val);
                    devkey.Close();
                }
                subkey.Close();
            }
            pci.Close();

            Console.ReadKey();
        }
    }
}