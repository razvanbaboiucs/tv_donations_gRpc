using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.VisualStyles;
using Grpc.Core;

namespace ClientSide
{
    public partial class MainForm : Form
    {
        private IServices.IServicesClient ServerService { get; set; }
        private IObserver.IObserverClient ObserverStub { get; set; }
        private AsyncDuplexStreamingCall<Donation, Response> Observer { get; set; }
        private Channel Channel { get; set; }
        
        public MainForm(IServices.IServicesClient services, Channel channel)
        {
            ServerService = services;
            Channel = channel;
            InitializeComponent();
            InitializeObserver();
            Init();
        }
        
        private void Init()
        {
            IList<Domain.Donor> donors = new List<Domain.Donor>();
            Response response = ServerService.findAllDonors(new Empty());
            foreach (var responseDonor in response.Donors)
            {
                donors.Add(new Domain.Donor(responseDonor.Id, responseDonor.Name, responseDonor.Address, responseDonor.PhoneNumber));
            }
            UpdateDonorsDonationAdded(donors);
           
            IList<Domain.Case> cases = new List<Domain.Case>();
            response = ServerService.findAllCases(new Empty());
            foreach (var responseCase in response.Cases)
            {
                cases.Add(new Domain.Case(responseCase.Id, responseCase.Name, responseCase.TotalSumDonated));
            }
            UpdateCasesDonationAdded(cases);
        }
        
        public delegate void UpdateDataGridCase(IEnumerable<Case> cases);

        public delegate void UpdateDataGridDonor(IEnumerable<Donor> donors);

        private void InitializeObserver()
        {
            ObserverStub = new IObserver.IObserverClient(Channel);
            Observer = ObserverStub.updateDonationAdded();
            var responseReaderTask = Task.Run(async () =>
            {
                while (await Observer.ResponseStream.MoveNext())
                {
                    Response response = Observer.ResponseStream.Current;
                    IList<Domain.Case> cases = new List<Domain.Case>();
                    foreach (var responseCase in response.Cases)
                    {
                        cases.Add(new Domain.Case(responseCase.Id, responseCase.Name, responseCase.TotalSumDonated));
                    }
                    IList<Domain.Donor> donors = new List<Domain.Donor>();
                    foreach (var responseDonor in response.Donors)
                    {
                        donors.Add(new Domain.Donor(responseDonor.Id, responseDonor.Name, responseDonor.Address, responseDonor.PhoneNumber));
                    }
                    UpdateCasesDonationAdded(cases);
                    UpdateDonorsDonationAdded(donors);
                }
            });
        }

        public void CloseObserver()
        {
            Observer.RequestStream.CompleteAsync();
        }
        
        private void UpdateCasesDonationAdded(IEnumerable<Domain.Case> casesList)
        {
            dataGridViewCases.Rows.Clear();
            IEnumerable<Domain.Case> cases = casesList;
            foreach (var currentCase in cases)
            {
                var index = dataGridViewCases.Rows.Add();
                dataGridViewCases.Rows[index].Cells["CaseName"].Value = currentCase.Name;
                dataGridViewCases.Rows[index].Cells["CaseTotalSumDonated"].Value = currentCase.TotalSumDonated;
                dataGridViewCases.Rows[index].Cells["CaseId"].Value = currentCase.Id;
            }
        }

        private void UpdateDonorsDonationAdded(IEnumerable<Domain.Donor> donorsList)
        {
            dataGridViewDonors.Rows.Clear();
            IEnumerable<Domain.Donor> donors = donorsList;
            foreach (var donor in donors)
            {
                var index = dataGridViewDonors.Rows.Add();
                dataGridViewDonors.Rows[index].Cells["DonorName"].Value = donor.Name;
                dataGridViewDonors.Rows[index].Cells["DonorAddress"].Value = donor.Address;
                dataGridViewDonors.Rows[index].Cells["DonorPhoneNumber"].Value = donor.PhoneNumber;
                dataGridViewDonors.Rows[index].Cells["DonorId"].Value = donor.Id;
            }
        }

        private void buttonDonate_Click(object sender, EventArgs e)
        {
            if (dataGridViewDonors.SelectedRows.Count == 0 || dataGridViewCases.SelectedRows.Count == 0)
            {
                MessageBox.Show("Please select a row from donors and one from cases!");
            }
            else
            {
                try
                {
                    int caseId = Int32.Parse(dataGridViewCases.SelectedRows[0].Cells["CaseId"].Value.ToString());
                    double sumToDonate = Double.Parse(textBoxSumToDonate.Text);
                    int donorId = 0;
                    if (dataGridViewDonors.SelectedRows[0].Cells["DonorName"].Value.ToString() == textBoxDonorName.Text)
                    {
                        donorId = Int32.Parse(dataGridViewDonors.SelectedRows[0].Cells["DonorId"].Value.ToString());
                    }
                    else
                    {
                        string name = textBoxDonorName.Text;
                        string address = textBoxDonorAddress.Text;
                        string phoneNumber = textBoxDonorPhoneNumber.Text;
                        Response response = ServerService.addDonor(new Donor{Id = 0, Address = address, Name = name, PhoneNumber = phoneNumber});
                        try
                        {
                            if (response.Type == Response.Types.Type.Error)
                            {
                                throw new Exception("Cannot add donor!");
                            }

                            donorId = response.Did;
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show(ex.Message);
                        }
                    }
                    try
                    {
                        Observer.RequestStream.WriteAsync(new Donation()
                        {
                            Did = donorId,
                            Cid = caseId,
                            SumDonated = sumToDonate
                        });
                    }
                    catch(Exception ex)
                    {
                        MessageBox.Show(ex.Message);
                    }         
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Data not inserted corectly!\n" + ex);
                }
            }
        }

        private void dataGridViewCases_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if(dataGridViewCases.SelectedRows.Count > 0)
                textBoxCaseName.Text = dataGridViewCases.SelectedRows[0].Cells["CaseName"].Value.ToString();
        }

        private void buttonSearch_Click(object sender, EventArgs e)
        {
            Response response = ServerService.findAllDonorsBySubstring(new Substring{Substring_ = textBoxDonorName.Text});
            try
            {
                if (response.Type == Response.Types.Type.Error)
                {
                    throw new Exception("Cannot collect donors with such substring!");
                }
                IList<Domain.Donor> donors = new List<Domain.Donor>();
                foreach (var responseDonor in response.Donors)
                {
                    donors.Add(new Domain.Donor(responseDonor.Id, responseDonor.Name, responseDonor.Address, responseDonor.PhoneNumber));
                }
                UpdateDonorsDonationAdded(donors);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void buttonLogout_Click(object sender, EventArgs e)
        {
            Close();
        }

        private void dataGridViewDonors_CellContentClick_1(object sender, DataGridViewCellEventArgs e)
        {
            if (dataGridViewDonors.SelectedRows.Count > 0)
            {
                textBoxDonorName.Text = dataGridViewDonors.SelectedRows[0].Cells["DonorName"].Value.ToString();
                textBoxDonorAddress.Text = dataGridViewDonors.SelectedRows[0].Cells["DonorAddress"].Value.ToString();
                textBoxDonorPhoneNumber.Text =
                    dataGridViewDonors.SelectedRows[0].Cells["DonorPhoneNumber"].Value.ToString();
            }
        }
    }
}