import UIKit
import SharedCode



class ViewController: UIViewController, ApplicationContractView {

    @IBOutlet weak var departurePicker: UIPickerView!
    @IBOutlet weak var arrivalPicker: UIPickerView!
    @IBOutlet private var label: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var activityIndicatorView: UIActivityIndicatorView!
    @IBOutlet weak var advancedSearchCollectionView: UICollectionView!
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    private var stationData: [String] = []
    private var departuresData: [DepartureInformation] = []
    private var advancedSearchChoices: [AdvancedSearchCellInformation] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        self.departurePicker.delegate = self
        self.departurePicker.dataSource = self
        self.arrivalPicker.delegate = self
        self.arrivalPicker.dataSource = self
        setupTableView()
        setupAdvancedSearchCollectionView()
        tableView.isHidden = true
    }
    
    func formatDateForAPI(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let dateString = dateFormatter.string(from: date)
        return dateString
    }
    
    func formatDateForDisplay(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd HH:mm"
        let dateString = dateFormatter.string(from: date)
        return dateString
    }
    
    @IBAction func showAdvancedSearch(_ sender: Any) {
        let storyboard = UIStoryboard(name: "AdvancedSearch", bundle: .main)
        let advancedSearchVC = storyboard.instantiateViewController(withIdentifier: "AdvancedSearchVC") as! AdvancedSearchViewController
        advancedSearchVC.delegate = self
        present(advancedSearchVC, animated: true)
    }
    
    @IBAction func onJourneySelected(_ sender: Any) {
        findJourneysPressed()
    }
    
    
    func findJourneysPressed() {
        tableView.isHidden = true
        activityIndicatorView.startAnimating()
        presenter.onButtonTapped()
    }
    
    func setLabel(text: String) {
        label.text = text
    }
    
    func showAlertMessage(alertMessage: String) {
        activityIndicatorView.stopAnimating()
        let alert = UIAlertController(title: "Error", message: alertMessage, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Okay", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    
}

extension ViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return stationData.count
    }
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return stationData[row]
    }
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if (pickerView.isEqual(departurePicker)) { setDepartureStation() }
        if (pickerView.isEqual(arrivalPicker)) { setArrivalStation() }
    }
    func setDepartureStation() {
        let departureStation: String = stationData[departurePicker.selectedRow(inComponent: 0)]
        presenter.setDepartureStation(departureStation: departureStation)
    }
    func setArrivalStation() {
        let arrivalStation: String = stationData[arrivalPicker.selectedRow(inComponent: 0)]
        presenter.setArrivalStation(arrivalStation: arrivalStation)
    }
    func setDepartureDropdown(stationList: Array<String>) {
        stationData = stationList
        setDepartureStation()
        self.departurePicker.reloadComponent(0)
        setDepartureStation()
    }
    func setArrivalDropdown(stationList: Array<String>) {
        stationData = stationList
        setArrivalStation()
        self.arrivalPicker.reloadComponent(0)
        setArrivalStation()
    }
}

extension ViewController: UITableViewDelegate, UITableViewDataSource {
    
    func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "cell")
        self.tableView.reloadData()
    }

    func populateDeparturesTable(departuresList: [DepartureInformation]) {
        departuresData = departuresList
        activityIndicatorView.stopAnimating()
        tableView.isHidden = false
        tableView.reloadData()
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return departuresData.count
    }
    func tableView(_ tableView: UITableView,
                 cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "departureCell", for: indexPath) as! DeparturesTableViewCell
        let departure = departuresData[indexPath.row]
        cell.departureLabel!.text = departure.departureTime
        cell.arrivalLabel!.text = departure.arrivalTime
        cell.durationLabel!.text = departure.journeyTime
        cell.priceLabel!.text = departure.price
        cell.operatorLabel!.text = departure.trainOperator
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80.0
    }
}

extension ViewController: UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    func setupAdvancedSearchCollectionView() {
        self.advancedSearchCollectionView.delegate = self
        self.advancedSearchCollectionView.dataSource = self
        self.advancedSearchCollectionView.register(AdvancedSearchViewCell.self, forCellWithReuseIdentifier: "cell")
        self.advancedSearchCollectionView.reloadData()
    }
    
    func populateAdvancedSearchCollection(choiceList: [AdvancedSearchCellInformation]) {
        advancedSearchChoices = choiceList
        advancedSearchCollectionView.reloadData()
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return advancedSearchChoices.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = advancedSearchCollectionView.dequeueReusableCell(withReuseIdentifier: "advancedSearchCell", for: indexPath) as! AdvancedSearchViewCell
        cell.delegate = self
        cell.choiceLabel!.text = advancedSearchChoices[indexPath.row].label
        cell.dataType = advancedSearchChoices[indexPath.row].dataType
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let cellWidthEstimate = advancedSearchChoices[indexPath.row].label.count * 10 + 25
        return CGSize(width: cellWidthEstimate, height: 28)
    }

}

extension ViewController: AdvancedSearchDelegate {
    func applyButtonPressed(numAdults: Int, numChildren: Int, date: Date) {
        let adultsChoice = "Adults: " + String(numAdults)
        let childrenChoice = "Children: " + String(numChildren)
        let dateChoice = formatDateForDisplay(date: date)
        populateAdvancedSearchCollection(choiceList: [
            AdvancedSearchCellInformation(label: adultsChoice, dataType: cellDataType.adults),
            AdvancedSearchCellInformation(label: childrenChoice, dataType: cellDataType.children),
            AdvancedSearchCellInformation(label: dateChoice, dataType: cellDataType.date)
                ])
        presenter.setNumAdults(numAdults: Int32(numAdults))
        presenter.setNumChildren(numChildren: Int32(numChildren))
        presenter.setDepartureTime(departureTime: formatDateForAPI(date: date))
        findJourneysPressed()
    }
}

extension ViewController: AdvancedSearchCollectionDelegate {
    func removeAdvancedSearchChoice(cell: AdvancedSearchViewCell) {
        let indexPath = advancedSearchCollectionView.indexPath(for: cell)
        advancedSearchChoices.remove(at: indexPath!.row)
        advancedSearchCollectionView.reloadData()
        switch cell.dataType {
        case .adults:
            presenter.setNumAdults(numAdults: 0)
        case .children:
            presenter.setNumChildren(numChildren: 0)
        case .date:
            let timeNow = formatDateForAPI(date: Date())
            presenter.setDepartureTime(departureTime: timeNow)
        }
    }
}
