import UIKit
import SharedCode


class AdvancedSearchViewController: UIViewController, AdvancedSearchContractView {

    var delegate: AdvancedSearchDelegate?
    private let presenter: AdvancedSearchPresenter = AdvancedSearchPresenter()
    
    @IBOutlet weak var applyButton: UIButton!
    @IBOutlet weak var dismissButton: UIButton!
    @IBOutlet weak var numberAdults: UILabel!
    @IBOutlet weak var numberChildren: UILabel!
    @IBOutlet weak var adultsStepper: UIStepper!
    @IBOutlet weak var childrenStepper: UIStepper!
    @IBOutlet weak var datePicker: UIDatePicker!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
    }
    
    @IBAction func didTapDismiss(_ sender: Any) {
        dismiss(animated: true)
    }
    @IBAction func didTapApply(_ sender: Any) {
        let dateString = formatDateAsString(date: datePicker.date)
        presenter.submitSearch(numAdults: Int32(adultsStepper.value), numChildren: Int32(childrenStepper.value), date: dateString)
    }
    
    @IBAction func incrementAdults(_ sender: Any) {
        numberAdults.text = String(format: "%d", Int(adultsStepper.value))
    }
    @IBAction func incrementChildren(_ sender: Any) {
        numberChildren.text = String(format: "%d", Int(childrenStepper.value))
    }
    func showAlertMessage(alertMessage: String) {
        let alert = UIAlertController(title: "Error", message: alertMessage, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Okay", style: .default, handler: nil))
        self.present(alert, animated: true, completion: nil)
    }
    func submitAdvancedSearch() {
        delegate?.applyButtonPressed(numAdults: Int(adultsStepper.value), numChildren: Int(childrenStepper.value), date: datePicker.date)
        dismiss(animated: true)
    }
    func formatDateAsString(date: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.000"
        return dateFormatter.string(from: date)
    }
}

protocol AdvancedSearchDelegate {
    func applyButtonPressed(numAdults: Int, numChildren: Int, date: Date)
}
