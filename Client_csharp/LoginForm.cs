using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Grpc.Core;

namespace ClientSide
{
    public partial class LoginForm : Form
    {
        private string Host { get; set; }
        private int Port { get; set; }
        public LoginForm()
        {
            InitializeComponent();
            Host = System.Configuration.ConfigurationSettings.AppSettings["host"];
            Port = Convert.ToInt32(System.Configuration.ConfigurationSettings.AppSettings["port"]);
        }


        private void buttonLogin_Click(object sender, EventArgs e)
        {
            string username = textBoxUsername.Text;
            string password = textBoxPassword.Text;
            Channel channel = new Channel(Host, Port, ChannelCredentials.Insecure);

            var client = new IServices.IServicesClient(channel);
            try
            {
                Volunteer volunteer = new Volunteer {Id = 0, User = username, Password = password};
                Response response = client.findVolunteer(volunteer);
                if (response.Type == Response.Types.Type.Error)
                {
                    throw new Exception("Credentials were wrong!");
                }

                using (MainForm mainForm = new MainForm(client, channel))
                {
                    Hide();
                    mainForm.ShowDialog();
                    mainForm.CloseObserver();
                }

                volunteer = new Volunteer {Id = response.Did, User = username, Password = password};
                response = client.logout(volunteer);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

            channel.ShutdownAsync();
            Close();
        }
    }
}